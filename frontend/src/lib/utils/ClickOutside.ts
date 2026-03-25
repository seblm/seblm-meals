import type { ActionReturn } from 'svelte/action';

export function clickOutside(node: HTMLElement): ActionReturn {
	const handleClick = (event: Event) => {
		const target = event.target as Node;
		if (node && (node === target || !node.contains(target)) && !event.defaultPrevented) {
			node.dispatchEvent(new CustomEvent('outclick'));
		}
	};

	document.addEventListener('click', handleClick, true);

	return {
		destroy() {
			document.removeEventListener('click', handleClick, true);
		}
	};
}
