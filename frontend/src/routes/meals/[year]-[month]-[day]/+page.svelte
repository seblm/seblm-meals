<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.ts';
	import { refreshAll } from '$app/navigation';
	import type { PageProps } from './$types';
	import Meal2 from '#lib/meal/Meal2.svelte';
	import { unlinkWithTime } from '#lib/utils/api.ts';
	import { resolve } from '$app/paths';
	import Flag from '@lucide/svelte/icons/flag';

	let { data }: PageProps = $props();

	let day = $derived(data.reference.slice(8, 10));
	let month = $derived(data.reference.slice(5, 7));
	let year = $derived(data.reference.slice(0, 4));
	let today = $derived(resolve('/[year]-[month]-[day]', { day, month, year }));

	async function unlink(time: string) {
		return unlinkWithTime(time).then(() => refreshAll());
	}
</script>

<div class="flex h-dvh flex-col overflow-hidden bg-background">
	<main class="flex-1 space-y-10 overflow-y-auto px-4 pt-4 pb-6" data-sveltekit-preload-data="tap">
		{#each data.weekMealsCenteredAroundADay.days as day (day.reference)}
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
	<footer class="z-50 shrink-0 border-t bg-background/95 p-4 backdrop-blur-md">
		<div class="mx-auto flex max-w-2xl items-center gap-3">
			<Button class="h-12 w-12 shrink-0 rounded-full bg-primary shadow-md" href={today} size="icon">
				<Flag />
			</Button>
		</div>
	</footer>
</div>
