import type { PageLoad } from './$types';
import { changeDay } from '$lib/calendar.svelte';
import { getWeekMealsCenteredAroundADay } from '$lib/utils/api';

export const load: PageLoad = ({ params }) => {
	const day = parseInt(params.day);
	const month = parseInt(params.month);
	const year = parseInt(params.year);
	changeDay({ day, month, year });

	return getWeekMealsCenteredAroundADay(day, month, year);
};
