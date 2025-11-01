<script lang="ts">
	import { resolve } from '$app/paths';

	interface Props {
		year: number;
		week: number;
	}

	let { year, week }: Props = $props();
</script>

<nav>
	<ul>
		<li>
			<strong>
				<a
					href={resolve('/meals/[year]/[week]', { year: year.toString(), week: week.toString() })}
					class="contrast"
				>
					seblm-meals
				</a>
			</strong>
		</li>
	</ul>
	<ul>
		<li><a href={resolve('/meals')} class="contrast">Tous les repas</a></li>
	</ul>
</nav>
