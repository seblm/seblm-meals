<script lang="ts">
	import type { PageProps } from './$types';
	import MealMenu from '$lib/MealMenu.svelte';
	import Meal from '$lib/meal/Meal.svelte';
	import { currentDay } from '$lib/calendar.svelte';

	let { data }: PageProps = $props();
</script>

<main class="container" data-sveltekit-preload-data="tap">
	<MealMenu
		day={currentDay.day}
		month={currentDay.month}
		year={currentDay.year}
		week={currentDay.weekNumber}
	/>
	<div class="grid">
		{#each data.days as day}
			<p>{day.reference}</p>
			<p>
				{#if day.lunch?.meal}<Meal meal={day.lunch?.meal} />{/if}
			</p>
			<p>
				{#if day.dinner?.meal}<Meal meal={day.dinner?.meal} />{/if}
			</p>
		{/each}
	</div>
</main>

<style>
	@media (min-width: 768px) {
		.grid {
			grid-template-columns: 1fr 3fr 3fr;
		}
	}
</style>
