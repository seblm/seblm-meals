import { Component } from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'seblm-meals';
  start = moment().locale('fr').startOf('week');
  end = moment().locale('fr').endOf('week');
  current(): string {
    console.log(this.currentMenus);
    if (this.start.month() == this.end.month()) {
      return `Du ${this.start.format('D')} au ${this.end.format('D MMMM YYYY')} `
    } else {
      return `Du ${this.start.format('D MMMM')} au ${this.end.format('D MMMM YYYY')} `
    }
  }
  currentMenus: Menu[] = [
      new Menu('lundi', 'galettes de légumes riz'),
      new Menu('mardi', 'chou-fleur pomme de terre lardons'),
      new Menu('mercredi midi', 'chou-fleur pomme de terre lardons'),
      new Menu('mercredi soir', 'chou-fleur pomme de terre lardons'),
      new Menu('jeudi', 'chou-fleur pomme de terre lardons'),
      new Menu('vendredi', 'chou-fleur pomme de terre lardons'),
      new Menu('samedi midi', 'chou-fleur pomme de terre lardons'),
      new Menu('samedi soir', 'chou-fleur pomme de terre lardons'),
      new Menu('dimanche midi', 'chou-fleur pomme de terre lardons'),
      new Menu('dimanche soir', 'chou-fleur pomme de terre lardons'),
    ]
}

export class Menu {
  constructor(public day: string, public meal: string) { }
}
