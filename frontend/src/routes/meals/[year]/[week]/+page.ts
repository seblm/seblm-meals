import type { PageLoad } from './$types';
import { getWeekMeals } from '#lib/utils/api.ts';
import type { WeekMeals } from '#lib/model/WeekMeals.ts';

export const load: PageLoad = ({ params }) => {
	const year = parseInt(params.year);
	const week = parseInt(params.week);

	if (isNaN(year) || isNaN(week)) {
		return Promise.reject<WeekMeals>(`year ${year} and/or week ${week} aren’t numbers`);
	} else {
		return getWeekMeals(year, week);
	}
};
