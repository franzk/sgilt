// app/types/category.ts
export interface Category {
  id: string
  name: string
  picto?: Component
  subcategories: SubCategory[]
}

export interface SubCategory {
  id: string
  name: string
  categoryId: string
  picto?: Component
}
