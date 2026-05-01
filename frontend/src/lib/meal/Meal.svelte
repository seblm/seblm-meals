<script lang="ts">
	import type { Meal } from '$lib/model/WeekMeals';
	import { resolve } from '$app/paths';

	interface Props {
		meal: Meal;
	}

	let { meal }: Props = $props();
</script>

<div class="meal-card">
	<img src="/assets/{meal.id}.webp" alt={meal.description} />
	<div class="footer">
		<a href={resolve('/meal/[id]', { id: meal.id })}>{meal.description}</a>
		{#if meal.url}
			<a
				class="icon fa7-regular--share-square"
				href={meal.url}
				rel="external"
				target="_blank"
				title="externalUrl"
			></a>
		{/if}
	</div>
</div>

<style>
	@import '../../routes/icon-share-square.css';
	.icon {
		height: 1rem;
		width: 1rem;
		vertical-align: top;
	}
	.meal-card {
		/* Add shadows to create the "card" effect */
		box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
		transition: 0.3s;
		border-radius: 1rem;
		.footer {
			padding: 2px 16px;
		}
		/* On mouse-over, add a deeper shadow */
		:hover {
			box-shadow: 0 8px 16px 0 rgba(0, 0, 0, 0.2);
		}
		/* Add rounded corners to the top left and the top right corner of the image */
		img {
			border-radius: 1rem 1rem 0 0;
		}
	}
</style>
