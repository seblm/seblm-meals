import type { PageLoad } from './$types';
import { getMeal } from '$lib/utils/api';

export const load: PageLoad = ({ params }) => {
	return getMeal(params.id);
};
