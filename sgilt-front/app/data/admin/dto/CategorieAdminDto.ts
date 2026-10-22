export interface SousCategorieAdminDto {
  key: string
  name: string
  categoryKey: string
  prestataireCount: number
}

export interface CategorieAdminDto {
  key: string
  name: string
  subcategories: SousCategorieAdminDto[]
}

export interface SousCategorieCreateRequestDto {
  key: string
  name: string
  categoryKey: string
}

export interface SousCategorieUpdateRequestDto {
  name: string
  categoryKey: string
}
