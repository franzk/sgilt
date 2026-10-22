export interface SubCategoryDto {
  key: string
  name: string
  categoryKey: string
}

export interface CategoryDto {
  key: string
  name: string
  subcategories: SubCategoryDto[]
}
