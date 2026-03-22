<script lang="ts">
	import WeekCalendar from './WeekCalendar.svelte';
	import { resolve } from '$app/paths';
</script>

<div class="calendar">
	<header class="calendar-header">
		<h1>Votre semaine <a href={resolve('/meals/2026/12')}>seblm-meals</a></h1>
	</header>
	<main class="calendar-content">
		<WeekCalendar />
	</main>
</div>

<style>
	.calendar {
		width: 100%;
		header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin: 24px 0;
			h1 {
				grid-column: 2 / span 1;
				margin: 0;
			}
		}
	}

	@media (min-width: 769px) {
		.calendar {
			header {
				display: grid;
				grid-template-columns: repeat(3, 1fr);
			}
		}
	}
</style>
