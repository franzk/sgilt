export interface SearchCategory {
  name: string
  tags: SearchCategoryTag[]
  selection: SearchCategoryTag[]
  tagListExpanded: boolean
}

export interface SearchCategoryTag {
  id: string
  name: string
}
