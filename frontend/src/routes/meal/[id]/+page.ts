import type { PageLoad } from './$types';
import { getMeal } from '#lib/utils/api.ts';

export const load: PageLoad = ({ params }) => {
	return getMeal(params.id);
};
