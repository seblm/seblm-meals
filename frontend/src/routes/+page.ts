import { dev } from '$app/environment';
import { redirect } from '@sveltejs/kit';

export const load = async () => {
	if (dev) {
		const currentDate = new Date();
		const januaryFirst = new Date(currentDate.getFullYear(), 0, 1);
		const numberOfDays = Math.floor(
			(currentDate.valueOf() - januaryFirst.valueOf()) / (24 * 60 * 60 * 1000)
		);
		const weekNumber = Math.ceil((currentDate.getDay() + 1 + numberOfDays) / 7);
		redirect(307, `/meals/${currentDate.getFullYear()}/${weekNumber}`);
	}
};
