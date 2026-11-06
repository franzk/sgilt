/**
 * Mapper — page publique de l'organisateur : DTO ↔ domaine
 */
import type { OrganizerPublicPageDto, PageContentDto } from '../dto/OrganizerPublicPageDto'
import type { OrganizerPublicPage, PageContentForm } from '../domain/OrganizerPublicPage'

function mapPageContent(dto: PageContentDto): PageContentForm {
  return {
    slug: dto.slug,
    tag: dto.tag ?? '',
    subtitle: dto.subtitle ?? '',
    sectionTitle: dto.sectionTitle ?? '',
    description: dto.description ?? '',
    importantInfo: dto.importantInfo,
    practicalInfo: dto.practicalInfo,
    pictos: dto.pictos,
  }
}

export function mapOrganizerPublicPage(dto: OrganizerPublicPageDto): OrganizerPublicPage {
  return {
    billetterieEnabled: dto.billetterieEnabled,
    page: dto.page
      ? { content: mapPageContent(dto.page.content), published: dto.page.published, slugLocked: dto.page.slugLocked }
      : undefined,
    ticketing: dto.ticketing ?? undefined,
    sales: dto.sales ?? undefined,
  }
}

// Champ texte vide → null, comme l'attend le back.
const emptyToNull = (value: string): string | null => value.trim() || null

export function mapPageContentRequest(form: PageContentForm): PageContentDto {
  return {
    slug: form.slug.trim(),
    tag: emptyToNull(form.tag),
    subtitle: emptyToNull(form.subtitle),
    sectionTitle: emptyToNull(form.sectionTitle),
    description: emptyToNull(form.description),
    importantInfo: form.importantInfo,
    practicalInfo: form.practicalInfo,
    pictos: form.pictos,
  }
}
