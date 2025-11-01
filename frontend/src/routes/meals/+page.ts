import type { PageLoad } from './$types';
import { getMeals } from '$lib/utils/api';

export const load: PageLoad = async () => {
	const mealsStatistics = await getMeals();
	return { mealsStatistics };
};
