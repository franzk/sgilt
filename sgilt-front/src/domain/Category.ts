export interface Category {
  name: string
  description: string
  tags: Tag[]
}

export interface Tag {
  id: string
  name: string
}
