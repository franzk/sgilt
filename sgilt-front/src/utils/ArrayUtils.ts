type KeyOf<T> = keyof T

export const groupBy = <T>(array: T[], key: KeyOf<T>): Record<string, T[]> => {
  return array.reduce((result: Record<string, T[]>, item: T) => {
    const groupKey = String(item[key])
    if (!result[groupKey]) {
      result[groupKey] = []
    }
    result[groupKey].push(item)
    return result
  }, {})
}

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
