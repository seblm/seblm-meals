<script lang="ts">
	import type { PageProps } from './$types';
	import MealMenu from '$lib/MealMenu.svelte';
	import Meal from '$lib/meal/Meal.svelte';
	import { resolve } from '$app/paths';
	import { currentDay } from '$lib/calendar.svelte';

	let { data }: PageProps = $props();
</script>

<main class="container" data-sveltekit-preload-data="tap">
	<MealMenu
		day={currentDay.day}
		month={currentDay.month}
		week={data.now.week}
		year={data.now.year}
	/>
	<h2>{data.titles.long}</h2>
	<div class="grid week">
		<p>lundi</p>
		<p>
			{#if data.monday.lunch}<Meal meal={data.monday.lunch.meal} />{/if}
		</p>
		<p>
			{#if data.monday.dinner}<Meal meal={data.monday.dinner.meal} />{/if}
		</p>
		<p>mardi</p>
		<p>
			{#if data.tuesday.lunch}<Meal meal={data.tuesday.lunch.meal} />{/if}
		</p>
		<p>
			{#if data.tuesday.dinner}<Meal meal={data.tuesday.dinner.meal} />{/if}
		</p>
		<p>mercredi</p>
		<p>
			{#if data.wednesday.lunch}<Meal meal={data.wednesday.lunch.meal} />{/if}
		</p>
		<p>
			{#if data.wednesday.dinner}<Meal meal={data.wednesday.dinner.meal} />{/if}
		</p>
		<p>jeudi</p>
		<p>
			{#if data.thursday.lunch}<Meal meal={data.thursday.lunch.meal} />{/if}
		</p>
		<p>
			{#if data.thursday.dinner}<Meal meal={data.thursday.dinner.meal} />{/if}
		</p>
		<p>vendredi</p>
		<p>
			{#if data.friday.lunch}<Meal meal={data.friday.lunch.meal} />{/if}
		</p>
		<p>
			{#if data.friday.dinner}<Meal meal={data.friday.dinner.meal} />{/if}
		</p>
		<p>samedi</p>
		<p>
			{#if data.saturday.lunch}<Meal meal={data.saturday.lunch.meal} />{/if}
		</p>
		<p>
			{#if data.saturday.dinner}<Meal meal={data.saturday.dinner.meal} />{/if}
		</p>
		<p>dimanche</p>
		<p>
			{#if data.sunday.lunch}<Meal meal={data.sunday.lunch.meal} />{/if}
		</p>
		<p>
			{#if data.sunday.dinner}<Meal meal={data.sunday.dinner.meal} />{/if}
		</p>
	</div>
	<nav>
		<ul>
			<li>
				<a
					href={resolve('/meals/[year]/[week]', {
						year: data.previous.year.toString(),
						week: data.previous.week.toString()
					})}
					role="button">Previous</a
				>
			</li>
		</ul>
		<ul>
			<li>
				<a
					href={resolve('/meals/[year]/[week]', {
						year: data.now.year.toString(),
						week: data.now.week.toString()
					})}
					role="button">Current</a
				>
			</li>
		</ul>
		<ul>
			<li>
				<a
					href={resolve('/meals/[year]/[week]', {
						year: data.next.year.toString(),
						week: data.next.week.toString()
					})}
					role="button">Next</a
				>
			</li>
		</ul>
	</nav>
</main>

<style>
	@import '../../../icon-share-square.css';
	.week {
		grid-template-columns: 1fr 3fr 3fr;
	}
</style>
