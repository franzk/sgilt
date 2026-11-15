<!--
  Lecteur de QR code de l'onglet Scan (scan des e-billets d'un événement) : la caméra arrière du
  téléphone (la webcam sur un ordinateur), dans le navigateur. Émet le texte brut de chaque QR lu ;
  ne sait rien des e-billets (le format SGILT:C:<code> est relu par le back).
  La caméra n'est accessible qu'en HTTPS (ou sur localhost). Si elle ne s'ouvre pas (refusée,
  absente, déjà prise…), un message dit pourquoi, avec un bouton Réessayer qui la rouvre.
  Bibliothèque : vue-qrcode-reader (détecteur natif du navigateur, ou zxing en WebAssembly sinon :
  Safari iPhone, Firefox, Chrome Windows…). Le fichier WebAssembly est servi par Sgilt (public/zxing-wasm/),
  pas par le CDN que la bibliothèque utilise par défaut.
  ⚠ MONTÉE DE VERSION de vue-qrcode-reader : vérifier la version de zxing-wasm qu'elle embarque
  (pnpm why zxing-wasm) ; si elle change, recopier node_modules/zxing-wasm/dist/reader/zxing_reader.wasm
  dans public/zxing-wasm/<version>/ et mettre à jour ZXING_WASM_VERSION (un fichier d'une autre version
  ne fonctionne pas).
-->
<template>
  <div class="ticket-scanner">
    <div v-if="failure" class="failure" role="alert">
      <p>{{ $t(`evenement.scan.camera.errors.${failure}`) }}</p>
      <SgiltButton variant="secondary" @click="retry">{{ $t('evenement.scan.camera.retry') }}</SgiltButton>
    </div>

    <div v-else class="viewfinder">
      <QrcodeStream
        :key="attempt"
        :constraints="CAMERA_CONSTRAINTS"
        :formats="QR_FORMATS"
        @camera-on="cameraOn = true"
        @detect="onDetect"
        @error="onError"
      />
      <p v-if="!cameraOn" class="opening">{{ $t('evenement.scan.camera.opening') }}</p>
    </div>
  </div>
</template>

<script lang="ts">
import { setZXingModuleOverrides } from 'vue-qrcode-reader'

// Version de zxing-wasm embarquée par vue-qrcode-reader (via barcode-detector) : celle du fichier servi.
const ZXING_WASM_VERSION = '1.1.3'

// Une seule fois, au chargement du module (pas à chaque lecteur : redéfinir les réglages oublierait
// le moteur déjà chargé), avant le premier décodage : le fichier WebAssembly est pris chez Sgilt
// plutôt que sur le CDN.
setZXingModuleOverrides({
  locateFile: (path: string, prefix: string) =>
    path.endsWith('.wasm') ? `/zxing-wasm/${ZXING_WASM_VERSION}/${path}` : prefix + path,
})
</script>

<script setup lang="ts">
import { QrcodeStream, type BarcodeFormat, type DetectedBarcode, type EmittedError } from 'vue-qrcode-reader'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

// Raison pour laquelle la caméra ne s'ouvre pas (clé du message).
type CameraFailure = 'NOT_ALLOWED' | 'NOT_FOUND' | 'NOT_READABLE' | 'INSECURE_CONTEXT' | 'NOT_SUPPORTED' | 'TECHNICAL'

// Caméra arrière de préférence ; « ideal » implicite : une webcam d'ordinateur convient aussi.
const CAMERA_CONSTRAINTS: MediaTrackConstraints = { facingMode: 'environment' }
const QR_FORMATS: BarcodeFormat[] = ['qr_code']

// Nom de l'erreur (DOMException du navigateur ou erreur de la bibliothèque) → message.
const FAILURE_BY_ERROR_NAME: Record<string, CameraFailure> = {
  NotAllowedError: 'NOT_ALLOWED',
  NotFoundError: 'NOT_FOUND',
  NotReadableError: 'NOT_READABLE',
  InsecureContextError: 'INSECURE_CONTEXT',
  StreamApiNotSupportedError: 'NOT_SUPPORTED',
  NotSupportedError: 'NOT_SUPPORTED',
}

const emit = defineEmits<{
  detect: [rawValue: string]
}>()

const failure = ref<CameraFailure | null>(null)
const cameraOn = ref(false)
// Change à chaque Réessayer : recrée le lecteur, qui redemande la caméra.
const attempt = ref(0)

function onDetect(codes: DetectedBarcode[]) {
  for (const code of codes) emit('detect', code.rawValue)
}

function onError(error: EmittedError) {
  failure.value = FAILURE_BY_ERROR_NAME[error.name] ?? 'TECHNICAL'
}

function retry() {
  failure.value = null
  cameraOn.value = false
  attempt.value++
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ticket-scanner {
  .failure {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-s;
    padding: $spacing-m;
    border-radius: $radius-lg;
    background: $surface-soft;

    p {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-primary;
    }
  }

  // Carré noir le temps que la caméra s'ouvre ; l'image la remplit ensuite (recadrée).
  .viewfinder {
    position: relative;
    overflow: hidden;
    aspect-ratio: 1;
    border-radius: $radius-lg;
    background: #000;

    .opening {
      position: absolute;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0;
      font-size: $font-size-sm;
      color: $surface-white;
    }
  }
}
</style>
