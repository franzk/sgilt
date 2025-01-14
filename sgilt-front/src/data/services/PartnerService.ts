import type { Partner } from '@/data/domain/Partner'
import type { PartnerQuery } from '@/data/api/query/PartnerQuery'
import {
  getRandomPartners,
  queryPartners,
  findPartnerBySlug,
} from '@/data/repository/PartnerRepository'

/**
 * Partner service
 */

/**
 * get highlighted partners
 * @param count the number of partners to get
 * @returns random {count} partners
 */
export const getHihglightedPartners = (count: number): Promise<Partner[]> => {
  return getRandomPartners(count)
}

/**
 * query partners
 * @param query the query to search partners
 * @returns a list of partners matching the query
 */
export const searchPartners = async (query: PartnerQuery): Promise<Partner[]> => {
  return queryPartners(query)
}

/**
 * get a partner by its slug
 * @param slug the partner
 * @returns the partner
 */
export const getPartnerBySlug = async (slug: string): Promise<Partner> => {
  return findPartnerBySlug(slug)
}
