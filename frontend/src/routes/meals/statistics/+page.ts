import type { PageLoad } from './$types';
import { getMealsStatistics } from '#lib/utils/api.ts';

export const load: PageLoad = async () => {
	const mealsStatistics = await getMealsStatistics();
	return { mealsStatistics };
};
