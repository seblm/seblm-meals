<script lang="ts">
	import { page } from '$app/state';
	import type { MealStatistics } from '$lib/model/WeekMeals';
	import { onMount } from 'svelte';
	import { getMeal } from '$lib/utils/api';
	import MealDate from '$lib/MealDate.svelte';

	let meal = $state({} as { meal?: MealStatistics });

	onMount(() => {
		let id = page?.params.id;
		if (id) {
			getMeal(id).then((mealFromServer) => {
				meal = { meal: mealFromServer };
			});
		}
	});
</script>

<main class="container">
	<nav aria-label="breadcrumb">
		<ul>
			<li><a href="/meals">All Meals</a></li>
			<li>{meal.meal?.meal.description}</li>
		</ul>
	</nav>
	<h2>{meal.meal?.meal.description} ({meal.meal?.count})</h2>
	<ul>
		{#if meal.meal?.meal.url}<li><a href={meal.meal?.meal.url}>Recipe</a></li>{/if}
		<li>Last usage: <MealDate date={meal.meal?.last} /></li>
		<li>First usage: <MealDate date={meal.meal?.first} /></li>
	</ul>
</main>
