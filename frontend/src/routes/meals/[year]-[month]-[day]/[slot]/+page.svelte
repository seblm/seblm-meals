<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.ts';
	import { Input } from '#lib/components/ui/input/index.ts';
	import MealAdd from '#lib/meal/MealAdd.svelte';
	import { getSuggestions, linkOrInsert } from '#lib/utils/api.ts';
	import ChevronLeft from '@lucide/svelte/icons/chevron-left';
	import type { PageProps } from './$types';

	let { data }: PageProps = $props();
	let suggestResponse = $derived(data.suggestResponse);
	let query = $state('');

	async function oninput(event: { currentTarget: { value: string } }) {
		query = event.currentTarget.value;

		if (!query.trim()) {
			suggestResponse = data.suggestResponse;
			return;
		}

		getSuggestions(data.day, query, data.isLunch).then((response) => (suggestResponse = response));
	}

	async function link(mealDescription: string): Promise<void> {
		return linkOrInsert(mealDescription, data.day, data.isLunch).then(() => history.back());
	}
</script>

<div class="flex h-dvh flex-col overflow-hidden bg-background">
	<main class="flex-1 overflow-y-auto px-4 pt-4 pb-6 mx-auto max-w-2xl space-y-10" data-sveltekit-preload-data="tap">
		<h2 class="text-xl font-bold">
			Add {#if data.isLunch}lunch{:else}dinner{/if} meal for {data.day}
		</h2>
		{#if suggestResponse.fourWeeksAgo}
			<MealAdd {link} mealSuggest={suggestResponse.fourWeeksAgo} special="Last month" />
		{/if}
		{#if suggestResponse.fiftyTwoWeeksAgo}
			<MealAdd {link} mealSuggest={suggestResponse.fiftyTwoWeeksAgo} special="Last year" />
		{/if}
		{#each suggestResponse.mostRecents as mealSuggest (mealSuggest.meal.id)}
			<MealAdd {link} {mealSuggest} />
		{/each}
	</main>
	<footer class="z-50 shrink-0 border-t bg-background/95 p-4 backdrop-blur-md">
		<div class="mx-auto flex max-w-2xl items-center gap-3">
			<Button
				class="h-12 w-12 shrink-0 rounded-full bg-primary shadow-md"
				onclick={() => history.back()}
				size="icon"
			>
				<ChevronLeft />
			</Button>
			<div class="flex-1">
				<Input {oninput} placeholder="Search..." value={query} class="h-12 w-full" />
			</div>
		</div>
	</footer>
</div>
