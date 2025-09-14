import BaseCasePage from "../components/BaseCasePage";
import DataList from "../components/DataList";
import { useRoutes } from 'react-router-dom';

const routes = [
    {
        path: '/',
        element: <DataList />
    },
    {
        path: '/case/:caseID/:memoryIDParam?',
        element: <BaseCasePage />
    }
]

export function RouterElement() {
    const element = useRoutes(routes);
    return element;
};