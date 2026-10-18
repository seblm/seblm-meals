import type { PageLoad } from './$types';
import { changeDay } from '#lib/calendar.svelte.ts';
import { getSuggestions } from '#lib/utils/api.ts';
import type { Model } from './model.ts';

export const load: PageLoad = ({ params }): Promise<Model> => {
	const day = parseInt(params.day);
	const month = parseInt(params.month);
	const year = parseInt(params.year);
	changeDay({ day, month, year });

	const refDate = `${params.year}-${params.month}-${params.day}`;
	const isLunch = params.slot === 'lunch';
	return getSuggestions(refDate, '', isLunch).then((suggestResponse) => {
		return {
			suggestResponse,
			day: refDate,
			isLunch
		};
	});
};
