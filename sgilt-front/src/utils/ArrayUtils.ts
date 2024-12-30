/**
 * returns an array of distinct values from an array of objects by a key
 * example: distinctByKey([{a: 1, b: 23}, {a: 2, b: 25}, {a: 1, b: 27}], 'a') => [1, 2]
 * @param array array of objects
 * @param key key to filter by
 * @returns array of distinct values
 */
export const distinctByKey = <T>(array: T[], key: keyof T): T[keyof T][] => {
  const seen = new Set<T[keyof T]>()

  return array
    .map((item) => item[key])
    .filter((value) => {
      if (seen.has(value)) {
        return false
      }
      seen.add(value)
      return true
    })
}
