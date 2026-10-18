<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.ts';
	import MealAdd from '#lib/meal/MealAdd.svelte';
	import { linkOrInsert } from '#lib/utils/api.ts';
	import ChevronLeft from '@lucide/svelte/icons/chevron-left';
	import type { PageProps } from './$types';

	let { data }: PageProps = $props();

	async function link(mealDescription: string): Promise<void> {
		return linkOrInsert(mealDescription, data.day, data.isLunch).then(
			() => {
				console.log('OK');
			},
			(error) => {
				console.log(error);
			}
		);
	}
</script>

<div class="min-h-screen bg-background">
	<main class="space-y-10 px-4" data-sveltekit-preload-data="tap">
		<Button
			class="fixed bottom-4 left-4 z-50 h-14 w-14 rounded-full bg-primary shadow-xl"
			onclick={() => history.back()}
			size="icon"
		>
			<ChevronLeft />
		</Button>
		<h2 class="mb-4 text-xl font-bold">
			Add {#if data.isLunch}lunch{:else}dinner{/if} meal for {data.day}
		</h2>
		{#if data.suggestResponse.fourWeeksAgo}
			<MealAdd {link} mealSuggest={data.suggestResponse.fourWeeksAgo} special="Last month" />
		{/if}
		{#if data.suggestResponse.fiftyTwoWeeksAgo}
			<MealAdd {link} mealSuggest={data.suggestResponse.fiftyTwoWeeksAgo} special="Last year" />
		{/if}
		{#each data.suggestResponse.mostRecents as mealSuggest (mealSuggest.meal.id)}
			<MealAdd {link} {mealSuggest} />
		{/each}
	</main>
</div>
