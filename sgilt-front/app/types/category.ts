// app/types/category.ts
export interface Category {
  id: string
  name: string
  picto: string
  subcategories: SubCategory[]
}

export interface SubCategory {
  id: string
  name: string
  categoryId: string
  picto: string
}
