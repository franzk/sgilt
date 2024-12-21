export interface SearchCategory {
  name: string
  tags: SearchCategoryTag[]
}

export interface SearchCategoryTag {
  id: string
  name: string
  checked: boolean
}
