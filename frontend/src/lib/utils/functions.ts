export const getDayName = (day: string): string => {
	switch (day) {
		case 'monday':
			return 'lundi';
		case 'tuesday':
			return 'mardi';
		case 'wednesday':
			return 'mercredi';
		case 'thursday':
			return 'jeudi';
		case 'friday':
			return 'vendredi';
		case 'saturday':
			return 'samedi';
		case 'sunday':
			return 'dimanche';
		default:
			return '';
	}
};
