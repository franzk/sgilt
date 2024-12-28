export interface Category {
  name: string
  imageUrl?: string
  tags: Tag[]
}

export interface Tag {
  id: string
  name: string
}
