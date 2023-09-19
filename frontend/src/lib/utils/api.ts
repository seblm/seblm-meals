import type { LinkOrInsert, SeachSuggestions, UnlinkMeal } from '$lib/model/WeekMeals';

export async function getWeekMeals(year: number, weekNumber: number) {
	return await fetch(`/api/meals/${year}/${weekNumber}`).then(
		(response) => response.json(),
		(error) => console.error(error)
	);
}

export async function getSuggestions(
	refDate: string,
	search: string,
	isLunch: boolean
): Promise<{ mostRecents: SeachSuggestions[] }> {
	return await fetch(
		`/api/suggest?reference=${refDate}%20${isLunch ? '12:00' : '20:00'}${
			search.length > 0 ? '&search=' + search : ''
		}`
	).then((response) => {
		return response.json();
	});
}

export async function linkOrInsert(mealDescription: string, day: string, isLunch: boolean) {
	let mealTime = `${day}T${isLunch ? '12' : '20'}:00:00.000Z`;

	const link: LinkOrInsert = {
		mealDescription,
		mealTime
	};
	return await fetch(`/api/link-or-insert`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(link)
	});
}

export async function unlink(day: string, isLunch: boolean) {
	const mealTime = `${day}T${isLunch ? '12' : '20'}:00:00.000Z`;
	const unlink: UnlinkMeal = {
		mealTime
	};
	return await fetch(`/api/unlink`, {
		method: 'DELETE',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(unlink)
	});
}
