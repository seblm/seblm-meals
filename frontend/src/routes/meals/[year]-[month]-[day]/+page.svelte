<script lang="ts">
	import { refreshAll } from '$app/navigation';
	import type { PageProps } from './$types';
	import Meal2 from '#lib/meal/Meal2.svelte';
	import { unlinkWithTime } from '#lib/utils/api.ts';
	import { resolve } from '$app/paths';

	let { data }: PageProps = $props();

	async function unlink(time: string) {
		return unlinkWithTime(time).then(() => refreshAll());
	}
</script>

<div class="min-h-screen bg-background">
	<main class="space-y-10 px-4" data-sveltekit-preload-data="tap">
		{#each data.days as day (day.reference)}
			<section>
				<h2 class="mb-4 text-xl font-bold">{day.reference}</h2>
				<div class="grid grid-cols-2 gap-4">
					{#each [day.lunch, day.dinner] as mealEntry, index (index)}
						<Meal2
							mealEntryOrLink={mealEntry ??
								resolve('/meals/[year]-[month]-[day]/[slot]', {
									day: day.reference.slice(8, 10),
									month: day.reference.slice(5, 7),
									slot: index === 0 ? 'lunch' : 'dinner',
									year: day.reference.slice(0, 4)
								})}
							unlink={(time: string) => unlink(time)}
						/>
					{/each}
				</div>
			</section>
		{/each}
	</main>
</div>
