<script lang="ts">
	import { onMount } from 'svelte';
	import { getMeals } from '$lib/utils/api';
	import type { MealStatistics } from '$lib/model/WeekMeals';

	let meals = $state([] as MealStatistics[]);

	onMount(() => {
		getMeals().then((mealsFromServer) => (meals = mealsFromServer));
	});
</script>

<main class="container">
	<nav aria-label="breadcrumb">
		<ul>
			<li>All Meals</li>
		</ul>
	</nav>

	<div class="overflow-auto">
		<table class="striped">
			<thead>
				<tr>
					<th>Count</th>
					<th>Description</th>
					<th>Last usage</th>
					<th>First usage</th>
					<th>Recipe</th>
				</tr>
			</thead>
			<tbody>
				{#each meals as meal (meal.meal.id)}
					{@const href = meal.meal.url}
					<!-- renaming meal.meal.url to href only to avoid no-navigation-without-resolve error -->
					<tr>
						<td>{meal.count}</td>
						<td>{meal.meal.description}</td>
						<td>
							{meal.last.toLocaleDateString('fr-FR')}
							{#if meal.last.getHours() === 12}midi{:else}soir{/if}
						</td>
						<td>
							{meal.first.toLocaleDateString('fr-FR')}
							{#if meal.first.getHours() === 12}midi{:else}soir{/if}
						</td>
						<td>
							{#if href}<a {href}>recipe</a>{/if}
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</main>
