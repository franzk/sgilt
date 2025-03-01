import { ref, onMounted, onUnmounted, type Ref } from 'vue'

/**
 *  Detects if the grid has multiple rows
 * @param containerRef - ref to the grid container
 */
export function useGridRowsDetection(containerRef: Ref<HTMLElement | null>) {
  // isMultiRow is true if the grid has multiple rows
  const isMultiRow = ref(false)

  // check if the grid has multiple rows
  const checkRows = () => {
    if (!containerRef.value) return
    const children = Array.from(containerRef.value.children) as HTMLElement[]
    const rowSet = new Set(children.map((child) => child.offsetTop))

    isMultiRow.value = rowSet.size > 1 // if there are multiple rows
  }

  // ResizeObserver to detect changes in the grid
  let observer: ResizeObserver

  onMounted(() => {
    checkRows()
    observer = new ResizeObserver(checkRows)
    observer.observe(containerRef.value!)
  })

  onUnmounted(() => {
    observer.disconnect()
  })

  return { isMultiRow }
}
