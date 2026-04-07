<script lang="ts">
	import MealDate from '$lib/MealDate.svelte';
	import type { PageProps } from './$types';
	import { resolve } from '$app/paths';
	import MealMenu from '$lib/MealMenu.svelte';
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
				{#each data.mealsStatistics as meal (meal.meal.id)}
					<!-- renaming meal.meal.url to href only to avoid no-navigation-without-resolve error -->
					{@const href = meal.meal.url}
					<tr>
						<td>{meal.count}</td>
						<td>
							<a href={resolve('/meal/[id]', { id: meal.meal.id })}>{meal.meal.description}</a>
						</td>
						<td><MealDate date={meal.last} /></td>
						<td><MealDate date={meal.first} /></td>
						<td>
							{#if href}
								<a
									class="fa7-regular--share-square icon"
									{href}
									rel="external"
									target="_blank"
									title="externalLink"
								></a>
							{/if}
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</main>

<style>
	@import '../../icon-share-square.css';
	.icon {
		height: 35px;
		width: 35px;
	}
</style>
