import type { PageLoad } from './$types';
import { changeDay } from '#lib/calendar.svelte.ts';
import { getWeekMealsCenteredAroundADay } from '#lib/utils/api.ts';

export const load: PageLoad = ({ params }) => {
	const day = parseInt(params.day);
	const month = parseInt(params.month);
	const year = parseInt(params.year);
	changeDay({ day, month, year });

	return getWeekMealsCenteredAroundADay(day, month, year);
};
