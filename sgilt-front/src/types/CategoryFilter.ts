export interface CategoryFilter {
  name: string
  tags: CategoryTagFilter[]
  selection: CategoryTagFilter[]
}

export interface CategoryTagFilter {
  id: string
  name: string
}
