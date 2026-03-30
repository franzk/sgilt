const label = useState<string | null>('contextBanner:label', () => null)
const abortFn = useState<(() => void) | null>('contextBanner:abortFn', () => null)

export function useContextBanner() {
  function show(l: string, fn: () => void) {
    label.value = l
    abortFn.value = fn
  }

  function hide() {
    label.value = null
    abortFn.value = null
  }

  return { label, abortFn, show, hide }
}
