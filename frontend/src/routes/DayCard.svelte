<script lang="ts">
	import { format } from 'date-fns';
	import { fr } from 'date-fns/locale';
	import type { MealDay } from '$lib/model/WeekMeals';

	export let day: Date;
	export let meals: MealDay | undefined;

	$: lunchValue = meals?.lunch?.meal;
	$: dinnerValue = meals?.dinner?.meal;
</script>

<div class="day-card">
	<h2 class="day-card-header">
		{format(day, 'eeee dd MMMM yyyy', {locale: fr})}
	</h2>
	<div class="day-card-content">
		<div class="day-card-content-meal day-card-content-meal-lunch">
			<h3 class="day-card-content-meal-title">Midi</h3>
			<p class="day-card-content-meal-value">
				{lunchValue ?? ''}
			</p>
		</div>
		<div class="day-card-content-meal day-card-content-meal-lunch">
			<h3 class="day-card-content-meal-title">Soir</h3>
			<p class="day-card-content-meal-value">
				{dinnerValue ?? ''}
			</p>
		</div>
	</div>
</div>

<style lang="scss">
	.day-card {
		border: 1px solid var(--color-theme-1);
		background: var(--color-bg-2);
		border-radius: var(--border-radius);
		&-header {
			padding: .5rem 1rem;
			margin: 0;
			border-bottom: 1px solid var(--color-theme-1);
		}
		&-content {
			padding: .5rem 1rem;
			&-meal {
				&-value {
					min-height: 5rem;
					padding: .5rem 1rem;
					box-shadow: var(--shadow) inset;
					border-radius: var(--border-radius);
				}
			}
		}
	}
</style>
