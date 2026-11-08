# Polices du PDF de l'e-billet

Polices de la charte Sgilt, embarquées dans le PDF (sous-ensemble des caractères utilisés) par
`EBilletPdfService`. Toutes sous SIL Open Font License 1.1 : licences à garder à côté des fichiers.

| Fichier | Origine | Licence |
|---|---|---|
| `Inter-Regular.ttf`, `Inter-SemiBold.ttf` | release [rsms/inter v4.1](https://github.com/rsms/inter/releases/tag/v4.1), `extras/ttf/` | `OFL-Inter.txt` |
| `CormorantGaramond-Bold.ttf` | instance statique `wght=700` du TTF variable de [google/fonts](https://github.com/google/fonts/tree/86aced91de05cd4f96a06838124cca45be337b2b/ofl/cormorantgaramond) (commit `86aced9`) | `OFL-CormorantGaramond.txt` |

Pourquoi des TTF statiques : PDFBox (moteur d'openhtmltopdf) n'embarque ni les polices variables
(il ne sait pas choisir une graisse) ni les OTF à contours CFF — or les releases de Cormorant ne
fournissent le Bold qu'en OTF ou en variable. L'instance a été produite avec fontTools 4.55.3 :

```
fonttools varLib.instancer "CormorantGaramond[wght].ttf" wght=700 --update-name-table -o CormorantGaramond-Bold.ttf
```
