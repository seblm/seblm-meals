<script lang="ts">
	import { invalidateAll } from '$app/navigation';
	import type { PageProps } from './$types';
	import { Button } from '#lib/components/ui/button';
	import Meal2 from '#lib/meal/Meal2.svelte';
	import { unlinkWithTime } from '#lib/utils/api';
	import ChevronLeft from '@lucide/svelte/icons/chevron-left';
	import ChevronRight from '@lucide/svelte/icons/chevron-right';
	import ListIndentIncrease from '@lucide/svelte/icons/list-indent-increase';

	let { data }: PageProps = $props();

	async function unlink(time: string) {
		return unlinkWithTime(time).then(() => invalidateAll());
	}
</script>

<div class="min-h-screen bg-background">
	<Button
		size="icon"
		class="fixed top-4 left-4 z-0 h-14 w-14 rounded-full bg-background/60 shadow-lg backdrop-blur-xl"
	>
		<ChevronLeft />
	</Button>
	<Button
		size="icon"
		class="fixed top-4 right-4 z-50 h-14 w-14 rounded-full bg-background/60 shadow-lg backdrop-blur-xl"
	>
		<ChevronRight />
	</Button>
	<Button
		size="icon"
		class="fixed right-4 bottom-4 z-50 h-14 w-14 rounded-full bg-primary shadow-xl"
	>
		<ListIndentIncrease />
	</Button>
	<main class="space-y-10 px-4 pt-20 pb-24" data-sveltekit-preload-data="tap">
		{#each data.days as day (day.reference)}
			<section>
				<h2 class="mb-4 text-xl font-bold">{day.reference}</h2>
				<div class="grid grid-cols-2 gap-4">
					{#each [day.lunch, day.dinner] as mealEntry, index (index)}
						<Meal2 {mealEntry} reference={day.reference} unlink={() => unlink(`${mealEntry?.time}`)} />
					{/each}
				</div>
			</section>
		{/each}
	</main>
</div>
