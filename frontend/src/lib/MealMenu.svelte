<script lang="ts">
	import { resolve } from '$app/paths';

	interface Props {
		day: number;
		month: number;
		week: number;
		year: number;
	}

	let { day, month, week, year }: Props = $props();

	const pad = (n: number): string => (n < 10 ? '0' + n : n.toString());

	let dayAsString = $derived(pad(day));
	let monthAsString = $derived(pad(month));
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
			<a
				href={resolve('/meals/[year]-[month]-[day]', {
					day: dayAsString,
					month: monthAsString,
					year: year.toString()
				})}
			>
				{dayAsString}/{monthAsString}/{year}
			</a>
		</li>
	</ul>
	<ul>
		<li><a href={resolve('/')} class="contrast">Semaine</a></li>
		<li><a href={resolve('/meals/statistics')} class="contrast">Tous les repas</a></li>
	</ul>
</nav>
