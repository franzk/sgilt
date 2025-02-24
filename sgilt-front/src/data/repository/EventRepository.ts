import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import dayjs from 'dayjs'
import { findPartnerBySlug } from '@/data/repository/PartnerRepository'

export const getTestEvent = async (): Promise<SgiltEvent> => {
  const creationDateTime = dayjs(new Date()).subtract(10, 'days').toDate()
  const testDateTime = dayjs(new Date()).add(10, 'days').toDate()
  const salleDesFetesDeRathsamhausen = await findPartnerBySlug('salle-des-fetes-rathsamhausen')
  const gypsyReedEnsemble = await findPartnerBySlug('gypsy-reed-ensemble')
  const foodTruckBurgers = await findPartnerBySlug('food-truck-burgers')

  return {
    title: 'Mon 60e anniversaire !',
    description:
      "J'organise la fête de mon 60e anniversaire à la salle des fêtes de Rathsamhausen.",
    location: 'Rathsamhausen',
    dateTime: testDateTime,
    eventType: 'Anniversaire',
    reservations: [
      {
        id: '1',
        partner: salleDesFetesDeRathsamhausen,
        price: salleDesFetesDeRathsamhausen!.prices![0]!,
        totalPrice: 1000,
        messages: [],
        createdAt: creationDateTime,
        status: 'approved',
      },
      {
        id: '2',
        partner: gypsyReedEnsemble,
        price: gypsyReedEnsemble!.prices![0]!,
        totalPrice: 1500,
        messages: [],
        createdAt: creationDateTime,
        status: 'pending',
      },
      {
        id: '2',
        partner: foodTruckBurgers,
        price: foodTruckBurgers!.prices![0]!,
        quantity: 50,
        totalPrice: 1500,
        messages: [],
        createdAt: creationDateTime,
        status: 'approved',
      },
    ],
  }
}
