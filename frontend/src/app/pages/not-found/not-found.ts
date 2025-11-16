import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import {PageShellComponent} from '@app/components/page-shell/page-shell';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterModule, PageShellComponent],
  templateUrl: './not-found.html',
  styleUrl: './not-found.css'
})

export class NotFound {}
