export interface CategoryFilter {
  name: string
  tags: CategoryTagFilter[]
  selection: CategoryTagFilter[]
  tagListExpanded: boolean
}

export interface CategoryTagFilter {
  id: string
  name: string
}
