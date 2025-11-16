import type { Routes } from '@angular/router';
import { AboutComponent } from '@app/pages/about/about';
import { LatestComponent } from '@app/pages/latest/latest';
import { TrendsComponent } from '@app/pages/trends/trends';
import { NotFound } from '@app/pages/not-found/not-found';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'about'
    },

    {
        path: 'about',
        component: AboutComponent,
        title: 'Weather Station - About'
    },

    {
        path: 'latest',
        component: LatestComponent,
        title: 'Weather Station - Latest'
    },

    {
        path: 'trends',
        component: TrendsComponent,
        title: 'Weather Station - Trends'
    },

    {
        path: '**',
        component: NotFound,
        title: 'Weather Station - Not Found'
    }
];
