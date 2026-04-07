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
	{#each data.days as day}
		<p>{day.reference}</p>
		{#if day.lunch?.meal}<p><Meal meal={day.lunch?.meal} /></p>{/if}
		{#if day.dinner?.time}<p><Meal meal={day.dinner?.meal} /></p>{/if}
	{/each}
</main>
