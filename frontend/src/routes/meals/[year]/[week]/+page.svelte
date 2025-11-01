<script lang="ts">
	import { externalLink } from '$lib/images/external-link';
	import type { PageProps } from './$types';
	import MealMenu from '$lib/MealMenu.svelte';
	import { resolve } from '$app/paths';

	let { data }: PageProps = $props();
</script>

<main class="container" data-sveltekit-preload-data="tap">
	<MealMenu year={data.now.year} week={data.now.week} />
	<h2>{data.titles.long}</h2>
	<div class="grid week">
		<p>lundi</p>
		<p>
			{#if data.monday.lunch}
				<a href={resolve('/meal/[id]', { id: data.monday.lunch.meal.id })}>
					{data.monday.lunch.meal.description}
				</a>
				{#if data.monday.lunch.meal.url}
					{@const href = data.monday.lunch.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>
			{#if data.monday.dinner}
				<a href={resolve('/meal/[id]', { id: data.monday.dinner.meal.id })}>
					{data.monday.dinner.meal.description}
				</a>
				{#if data.monday.dinner.meal.url}
					{@const href = data.monday.dinner.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>mardi</p>
		<p>
			{#if data.tuesday.lunch}
				<a href={resolve('/meal/[id]', { id: data.tuesday.lunch.meal.id })}>
					{data.tuesday.lunch.meal.description}
				</a>
				{#if data.tuesday.lunch.meal.url}
					{@const href = data.tuesday.lunch.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>
			{#if data.tuesday.dinner}
				<a href={resolve('/meal/[id]', { id: data.tuesday.dinner.meal.id })}>
					{data.tuesday.dinner.meal.description}
				</a>
				{#if data.tuesday.dinner.meal.url}
					{@const href = data.tuesday.dinner.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>mercredi</p>
		<p>
			{#if data.wednesday.lunch}
				<a href={resolve('/meal/[id]', { id: data.wednesday.lunch.meal.id })}>
					{data.wednesday.lunch.meal.description}
				</a>
				{#if data.wednesday.lunch.meal.url}
					{@const href = data.wednesday.lunch.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>
			{#if data.wednesday.dinner}
				<a href={resolve('/meal/[id]', { id: data.wednesday.dinner.meal.id })}>
					{data.wednesday.dinner.meal.description}
				</a>
				{#if data.wednesday.dinner.meal.url}
					{@const href = data.wednesday.dinner.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>jeudi</p>
		<p>
			{#if data.thursday.lunch}
				<a href={resolve('/meal/[id]', { id: data.thursday.lunch.meal.id })}>
					{data.thursday.lunch.meal.description}
				</a>
				{#if data.thursday.lunch.meal.url}
					{@const href = data.thursday.lunch.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>
			{#if data.thursday.dinner}
				<a href={resolve('/meal/[id]', { id: data.thursday.dinner.meal.id })}>
					{data.thursday.dinner.meal.description}
				</a>
				{#if data.thursday.dinner.meal.url}
					{@const href = data.thursday.dinner.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>vendredi</p>
		<p>
			{#if data.friday.lunch}
				<a href={resolve('/meal/[id]', { id: data.friday.lunch.meal.id })}>
					{data.friday.lunch.meal.description}
				</a>
				{#if data.friday.lunch.meal.url}
					{@const href = data.friday.lunch.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>
			{#if data.friday.dinner}
				<a href={resolve('/meal/[id]', { id: data.friday.dinner.meal.id })}>
					{data.friday.dinner.meal.description}
				</a>
				{#if data.friday.dinner.meal.url}
					{@const href = data.friday.dinner.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>samedi</p>
		<p>
			{#if data.saturday.lunch}
				<a href={resolve('/meal/[id]', { id: data.saturday.lunch.meal.id })}>
					{data.saturday.lunch.meal.description}
				</a>
				{#if data.saturday.lunch.meal.url}
					{@const href = data.saturday.lunch.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>
			{#if data.saturday.dinner}
				<a href={resolve('/meal/[id]', { id: data.saturday.dinner.meal.id })}>
					{data.saturday.dinner.meal.description}
				</a>
				{#if data.saturday.dinner.meal.url}
					{@const href = data.saturday.dinner.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>dimanche</p>
		<p>
			{#if data.sunday.lunch}
				<a href={resolve('/meal/[id]', { id: data.sunday.lunch.meal.id })}>
					{data.sunday.lunch.meal.description}
				</a>
				{#if data.sunday.lunch.meal.url}
					{@const href = data.sunday.lunch.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
		</p>
		<p>
			{#if data.sunday.dinner}
				<a href={resolve('/meal/[id]', { id: data.sunday.dinner.meal.id })}>
					{data.sunday.dinner.meal.description}
				</a>
				{#if data.sunday.dinner.meal.url}
					{@const href = data.sunday.dinner.meal.url}
					<a {href} target="_blank">{@html externalLink}</a>
				{/if}
			{/if}
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
					role="button"
				>
					Previous
				</a>
			</li>
		</ul>
		<ul>
			<li>
				<a
					href={resolve('/meals/[year]/[week]', {
						year: data.now.year.toString(),
						week: data.now.week.toString()
					})}
					role="button"
				>
					Current
				</a>
			</li>
		</ul>
		<ul>
			<li>
				<a
					href={resolve('/meals/[year]/[week]', {
						year: data.next.year.toString(),
						week: data.next.week.toString()
					})}
					role="button"
				>
					Next
				</a>
			</li>
		</ul>
	</nav>
</main>

<style>
	.week {
		grid-template-columns: 1fr 3fr 3fr;
	}
</style>
