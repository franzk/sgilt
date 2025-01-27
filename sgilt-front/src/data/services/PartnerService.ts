import type { CalendarEntry, Partner } from '@/data/domain/Partner'
import type { PartnerQuery } from '@/data/api/query/PartnerQuery'
import type { PartnerSearchViewModel } from '../domain/viewmodels/PartnerSearchViewModel'
import {
  getRandomPartners,
  queryPartners,
  findPartnerBySlug,
  partnerCalendar,
  relatedPartners,
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
export const searchPartners = async (query: PartnerQuery): Promise<PartnerSearchViewModel[]> => {
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

/**
 * get partner calendar
 * @param partnerId
 * @returns the partner booked and option dates
 */
export const getPartnerCalendar = async (partnerId: string): Promise<CalendarEntry[]> => {
  return partnerCalendar(partnerId)
}

/**
 * get releated partners
 * @param partnerId
 * @returns partners that are releated to the given partner
 */
export const getReleatedPartners = async (
  partnerId: string,
  dateReservation?: Date,
): Promise<PartnerSearchViewModel[]> => {
  return relatedPartners(partnerId, dateReservation)
}
