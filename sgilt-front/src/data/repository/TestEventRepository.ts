import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import dayjs from 'dayjs'
import { findPartnerBySlug } from '@/data/repository/PartnerRepository'

export const getTestEvent = async (id: string): Promise<SgiltEvent> => {
  const creationDateTime = dayjs().subtract(10, 'days').toDate()
  const eventDateTime = dayjs().add(15, 'days').toDate()

  const salleDesFetesDeRathsamhausen = await findPartnerBySlug('salle-des-fetes-rathsamhausen')
  const gypsyReedEnsemble = await findPartnerBySlug('gypsy-reed-ensemble')
  const foodTruckBurgers = await findPartnerBySlug('food-truck-burgers')
  const giteDeLaMangouste = await findPartnerBySlug('gite-de-la-mangouste')
  const photoboothVintage = await findPartnerBySlug('photobooth-vintage')
  const djAnimation = await findPartnerBySlug('dj-animation')
  const eclatGourmet = await findPartnerBySlug('eclat-gourmet')
  const chateauMariage = await findPartnerBySlug('chateau-pour-mariage')

  const testEvents = [
    {
      id: '1',
      title: 'Mariage de Sophie & Julien',
      description:
        "Un mariage inoubliable dans un cadre d'exception avec des prestataires de choix.",
      location: 'Ratshamhausen',
      dateTime: eventDateTime,
      eventType: 'Mariage',
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
          price: gypsyReedEnsemble!.prices![1]!, // Formule 4 musiciens
          totalPrice: 1200,
          messages: [],
          createdAt: creationDateTime,
          status: 'pending',
        },
        {
          id: '3',
          partner: foodTruckBurgers,
          price: foodTruckBurgers!.prices![0]!,
          quantity: 80,
          totalPrice: 2400, // 30€/personne x 80 personnes
          messages: [],
          createdAt: creationDateTime,
          status: 'paid',
        },
        {
          id: '4',
          partner: giteDeLaMangouste,
          price: giteDeLaMangouste!.prices![0]!,
          totalPrice: 300,
          messages: [],
          createdAt: creationDateTime,
          status: 'approved',
        },
        {
          id: '5',
          partner: photoboothVintage,
          price: photoboothVintage!.prices![0]!,
          totalPrice: 400,
          messages: [],
          createdAt: creationDateTime,
          status: 'approved',
        },
        {
          id: '6',
          partner: djAnimation,
          price: djAnimation!.prices![3]!, // Animation 4h
          totalPrice: 500,
          messages: [],
          createdAt: creationDateTime,
          status: 'pending',
        },
        {
          id: '7',
          partner: eclatGourmet,
          price: eclatGourmet!.prices![1]!, // Menu 2 à 70€/pers
          quantity: 100,
          totalPrice: 7000, // 70€/personne x 100 personnes
          messages: [],
          createdAt: creationDateTime,
          status: 'approved',
        },
        {
          id: '8',
          partner: chateauMariage,
          price: chateauMariage!.prices![0]!,
          totalPrice: 5000,
          messages: [],
          createdAt: creationDateTime,
          status: 'approved',
        },
      ],
    },
    {
      id: '2',
      title: 'Mon 60e Anniversaire',
      description: '',
      location: 'Ratshamhausen',
      dateTime: eventDateTime,
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
          price: gypsyReedEnsemble!.prices![1]!, // Formule 4 musiciens
          totalPrice: 1200,
          messages: [],
          createdAt: creationDateTime,
          status: 'pending',
        },
        {
          id: '3',
          partner: foodTruckBurgers,
          price: foodTruckBurgers!.prices![0]!,
          quantity: 80,
          totalPrice: 2400, // 30€/personne x 80 personnes
          messages: [],
          createdAt: creationDateTime,
          status: 'approved',
        },
      ],
    },
  ]

  return testEvents.find((event) => event.id === id.toString()) as SgiltEvent
}
