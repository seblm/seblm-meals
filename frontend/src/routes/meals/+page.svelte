<script lang="ts">
	import { externalLink } from '$lib/images/external-link';
	import MealDate from '$lib/MealDate.svelte';
	import type { PageProps } from './$types';
	import { resolve } from '$app/paths';
	import MealMenu from '$lib/MealMenu.svelte';

	let { data }: PageProps = $props();
</script>

<main class="container" data-sveltekit-preload-data="tap">
	<MealMenu year={2025} week={44} />
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
							{#if href}<a {href} target="_blank">{@html externalLink}</a>{/if}
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</main>
