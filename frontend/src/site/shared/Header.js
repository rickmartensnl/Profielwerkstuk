/* This example requires Tailwind CSS v2.0+ */
import React, { Fragment } from 'react'
import { Popover, Transition, Menu } from '@headlessui/react'
import {
    ShieldExclamationIcon,
    MenuIcon,
    CalculatorIcon,
    PlayIcon,
    XIcon,
    LogoutIcon,
    AcademicCapIcon,
    CogIcon,
} from '@heroicons/react/outline'
import { ChevronDownIcon } from '@heroicons/react/solid'
import { Link } from "react-router-dom";

function classNames(...classes) {
    return classes.filter(Boolean).join(' ')
}

export class Header extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            loggedIn: false
        };

        this.authMiddleware = this.props.data.authMiddleware;
    }

    getCallToActions() {
        return [
            { name: 'Begin met leren', href: '/app', icon: PlayIcon },
            { name: 'Bug rapporteren', href: '/#reportbug', icon: ShieldExclamationIcon },
        ];
    }

    getSolutions() {
        return [
            {
                name: 'Wiskunde B',
                description: 'Start gelijk met leren van Wiskunde B!',
                href: '/#wib',
                icon: CalculatorIcon,
            }
        ];
    }

    componentDidUpdate(prevProps) {
        if (prevProps.data.loggedIn === true && this.state.loggedIn !== true) {
            this.setState({
                loggedIn: true,
            });
        }
    }

    render() {
        return (
            <Popover className="relative bg-gray-100 dark:bg-dark-secondary mb-5">
                <div className="max-w-7xl mx-auto px-4 sm:px-6">
                    <div className="flex justify-between items-center border-b-2 border-gray-200 dark:border-dark-primary py-6 md:justify-start md:space-x-10">
                        <div className="flex justify-start lg:w-0 lg:flex-1">
                            <Link to="/">
                                <span className="sr-only">Profielwerkstuk</span>
                                <img className="h-8 w-auto sm:h-10" src="https://tailwindui.com/img/logos/workflow-mark-indigo-600.svg" alt=""/>
                            </Link>
                        </div>
                        <div className="-mr-2 -my-2 md:hidden">
                            <Popover.Button className="bg-gray-100 dark:bg-dark-secondary rounded-md p-2 inline-flex items-center justify-center text-gray-400 dark:text-dark-text-secondary hover:text-gray-500 dark:hover:text-dark-text-primary hover:bg-white dark:hover:bg-dark-primary focus:outline-none focus:ring-0">

                                <span className="sr-only">Open menu</span>
                                <MenuIcon className="h-6 w-6" aria-hidden="true" />
                            </Popover.Button>
                        </div>
                        <Popover.Group as="nav" className="hidden md:flex space-x-10">
                            <Popover className="relative">
                                {({ open }) => (
                                    <>
                                        <Popover.Button
                                            className={classNames(
                                                open ? 'text-gray-700 dark:text-dark-text-primary' : 'text-gray-900 dark:text-dark-text-secondary',
                                                'group rounded-md inline-flex items-center text-base font-medium hover:text-gray-700 dark:hover:text-dark-text-primary focus:outline-none'
                                            )}
                                        >
                                            <span>Solutions</span>
                                            <ChevronDownIcon
                                                className={classNames(
                                                    open ? 'text-gray-700 dark:text-dark-text-primary' : 'text-gray-900 dark:text-dark-text-secondary',
                                                    'ml-2 h-5 w-5 group-hover:text-gray-500 dark:group-hover:text-dark-text-primary'
                                                )}
                                                aria-hidden="true"
                                            />
                                        </Popover.Button>

                                        <Transition
                                            as={Fragment}
                                            enter="transition ease-out duration-200"
                                            enterFrom="opacity-0 translate-y-1"
                                            enterTo="opacity-100 translate-y-0"
                                            leave="transition ease-in duration-150"
                                            leaveFrom="opacity-100 translate-y-0"
                                            leaveTo="opacity-0 translate-y-1"
                                        >
                                            <Popover.Panel className="absolute z-10 -ml-4 mt-3 transform px-2 w-screen max-w-md sm:px-0 lg:ml-0 lg:left-1/2 lg:-translate-x-1/2">
                                                <div className="rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 overflow-hidden">
                                                    <div className="relative grid gap-6 bg-white dark:bg-dark-primary px-5 py-6 sm:gap-8 sm:p-8">
                                                        {this.getSolutions().map((item) => (
                                                            <Link key={item.name} to={item.href} className="-m-3 p-3 flex items-start rounded-lg hover:bg-gray-50 dark:hover:bg-dark-secondary">
                                                                <item.icon className="flex-shrink-0 h-6 w-6 text-blue-500" aria-hidden="true" />
                                                                <div className="ml-4">
                                                                    <p className="text-base font-medium text-gray-900 dark:text-dark-text-primary">{item.name}</p>
                                                                    <p className="mt-1 text-sm text-gray-500 dark:text-dark-text-secondary">{item.description}</p>
                                                                </div>
                                                            </Link>
                                                        ))}
                                                    </div>
                                                    <div className="px-5 py-5 bg-gray-50 dark:bg-dark-primary-2 space-y-6 sm:flex sm:space-y-0 sm:space-x-10 sm:px-8">
                                                        {this.getCallToActions().map((item) => (
                                                            <div key={item.name} className="flow-root">
                                                                <Link to={item.href} className="-m-3 p-3 flex items-center rounded-md text-base font-medium text-gray-900 dark:text-dark-text-secondary hover:bg-gray-100 dark:hover:bg-dark-secondary">
                                                                    <item.icon className="flex-shrink-0 h-6 w-6 text-gray-400" aria-hidden="true" />
                                                                    <span className="ml-3">{item.name}</span>
                                                                </Link>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            </Popover.Panel>
                                        </Transition>
                                    </>
                                )}
                            </Popover>

                            <Link to="/#pricing" className="text-base font-medium text-gray-900 dark:text-dark-text-secondary hover:text-gray-700 dark:hover:text-dark-text-primary">
                                Pricing
                            </Link>
                            <Link to="/#docs" className="text-base font-medium text-gray-900 dark:text-dark-text-secondary hover:text-gray-700 dark:hover:text-dark-text-primary">
                                Docs
                            </Link>
                        </Popover.Group>
                        <div className="hidden md:flex items-center justify-end md:flex-1 lg:w-0">
                            {this.state.loggedIn ? (
                                <Menu as="div" className="relative inline-block text-left">
                                    <div>
                                        <Menu.Button className="inline-flex justify-center w-full px-4 py-2 text-sm font-medium text-white bg-black rounded-md bg-opacity-20 hover:bg-opacity-30 focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75">
                                            Mijn Account
                                            <ChevronDownIcon
                                                className="w-5 h-5 ml-2 -mr-1 text-violet-200 hover:text-violet-100"
                                                aria-hidden="true"
                                            />
                                        </Menu.Button>
                                    </div>
                                    <Transition
                                        as={Fragment}
                                        enter="transition ease-out duration-100"
                                        enterFrom="transform opacity-0 scale-95"
                                        enterTo="transform opacity-100 scale-100"
                                        leave="transition ease-in duration-75"
                                        leaveFrom="transform opacity-100 scale-100"
                                        leaveTo="transform opacity-0 scale-95"
                                    >
                                        <Menu.Items className="absolute right-0 w-56 mt-2 origin-top-right bg-white dark:bg-dark-primary divide-y divide-gray-100 dark:divide-dark-tertiary rounded-md shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                                            <div className="px-1 py-1 ">
                                                <Menu.Item>
                                                    <Link to="/app" className={`dark:text-dark-text-secondary dark:hover:text-dark-text-primary group flex rounded-md items-center w-full px-2 py-2 text-sm`}>
                                                        <AcademicCapIcon className="w-5 h-5 mr-2" />
                                                        Leren!
                                                    </Link>
                                                </Menu.Item>
                                            </div>
                                            <div className="px-1 py-1 ">
                                                <Menu.Item>
                                                    <Link to="/settings" className={`dark:text-dark-text-secondary dark:hover:text-dark-text-primary group flex rounded-md items-center w-full px-2 py-2 text-sm`}>
                                                        <CogIcon className="w-5 h-5 mr-2" />
                                                        Instellingen
                                                    </Link>
                                                </Menu.Item>
                                            </div>
                                            <div className="px-1 py-1">
                                                <Menu.Item>
                                                    <Link to="/logout" className={`text-red-500 hover:text-red-400 dark:hover:text-red-600 group flex rounded-md items-center w-full px-2 py-2 text-sm`}>
                                                        <LogoutIcon className="w-5 h-5 mr-2" />
                                                        Uitloggen
                                                    </Link>
                                                </Menu.Item>
                                            </div>
                                        </Menu.Items>
                                    </Transition>
                                </Menu>
                            ) : (
                                <div>
                                    <Link to="/login" className="whitespace-nowrap text-base font-medium text-gray-900 dark:text-dark-text-secondary hover:text-gray-700 dark:hover:text-dark-text-primary">
                                        Sign in
                                    </Link>
                                    <Link to="/register" className="ml-8 whitespace-nowrap inline-flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-500 hover:bg-blue-600">
                                        Sign up
                                    </Link>
                                </div>)
                            }
                        </div>
                    </div>
                </div>

                <Transition
                    as={Fragment}
                    enter="duration-200 ease-out"
                    enterFrom="opacity-0 scale-95"
                    enterTo="opacity-100 scale-100"
                    leave="duration-100 ease-in"
                    leaveFrom="opacity-100 scale-100"
                    leaveTo="opacity-0 scale-95"
                >
                    <Popover.Panel focus className="absolute z-50 top-0 inset-x-0 p-2 transition transform origin-top-right md:hidden">
                        <div className="rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 bg-white dark:bg-dark-primary divide-y-2 divide-gray-50 dark:divide-dark-tertiary">
                            <div className="pt-5 pb-6 px-5">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <img className="h-8 w-auto" src="https://tailwindui.com/img/logos/workflow-mark-indigo-600.svg" alt="Workflow"/>
                                    </div>
                                    <div className="-mr-2">
                                        <Popover.Button className="bg-white dark:bg-dark-primary rounded-md p-2 inline-flex items-center justify-center text-gray-900 dark:text-dark-text-secondary hover:text-gray-700 dark:hover:text-dark-text-primary hover:bg-gray-100 dark:hover:bg-dark-secondary focus:outline-none focus:ring-0">
                                            <span className="sr-only">Close menu</span>
                                            <XIcon className="h-6 w-6" aria-hidden="true" />
                                        </Popover.Button>
                                    </div>
                                </div>
                                <div className="mt-6">
                                    <nav className="grid gap-y-8">
                                        {this.getSolutions().map((item) => (
                                            <Link key={item.name} to={item.href} className="-m-3 p-3 flex items-center rounded-md hover:bg-gray-50 dark:hover:bg-dark-secondary">
                                                <item.icon className="flex-shrink-0 h-6 w-6 text-blue-500" aria-hidden="true" />
                                                <span className="ml-3 text-base font-medium text-gray-900 dark:text-dark-text-secondary">{item.name}</span>
                                            </Link>
                                        ))}
                                    </nav>
                                </div>
                            </div>
                            <div className="py-6 px-5 space-y-6">
                                <div className="grid grid-cols-2 gap-y-4 gap-x-8">
                                    <Link to="/#pricing" className="text-base font-medium text-gray-900 dark:text-dark-text-secondary hover:text-gray-700 dark:hover:text-dark-text-primary ">
                                        Pricing
                                    </Link>

                                    <Link to="/#docs" className="text-base font-medium text-gray-900 dark:text-dark-text-secondary hover:text-gray-700 dark:hover:text-dark-text-primary ">
                                        Docs
                                    </Link>
                                </div>
                                <div>
                                    {this.state.loggedIn ? (
                                        <Menu as="div" className="relative inline-block text-left">
                                            <div>
                                                <Menu.Button className="inline-flex justify-center w-full px-4 py-2 text-sm font-medium text-white bg-black rounded-md bg-opacity-20 hover:bg-opacity-30 focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75">
                                                    Mijn Account
                                                    <ChevronDownIcon
                                                        className="w-5 h-5 ml-2 -mr-1 text-violet-200 hover:text-violet-100"
                                                        aria-hidden="true"
                                                    />
                                                </Menu.Button>
                                            </div>
                                            <Transition
                                                as={Fragment}
                                                enter="transition ease-out duration-100"
                                                enterFrom="transform opacity-0 scale-95"
                                                enterTo="transform opacity-100 scale-100"
                                                leave="transition ease-in duration-75"
                                                leaveFrom="transform opacity-100 scale-100"
                                                leaveTo="transform opacity-0 scale-95"
                                            >
                                                <Menu.Items className="absolute left-0 w-56 mt-2 origin-top-right bg-white dark:bg-dark-primary-2 divide-y divide-gray-100 dark:divide-dark-tertiary rounded-md shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                                                    <div className="px-1 py-1 ">
                                                        <Menu.Item>
                                                            <Link to="/app" className={`dark:text-dark-text-secondary dark:hover:text-dark-text-primary group flex rounded-md items-center w-full px-2 py-2 text-sm`}>
                                                                <AcademicCapIcon className="w-5 h-5 mr-2" />
                                                                Leren!
                                                            </Link>
                                                        </Menu.Item>
                                                    </div>
                                                    <div className="px-1 py-1 ">
                                                        <Menu.Item>
                                                            <Link to="/settings" className={`dark:text-dark-text-secondary dark:hover:text-dark-text-primary group flex rounded-md items-center w-full px-2 py-2 text-sm`}>
                                                                <CogIcon className="w-5 h-5 mr-2" />
                                                                Instellingen
                                                            </Link>
                                                        </Menu.Item>
                                                    </div>
                                                    <div className="px-1 py-1">
                                                        <Menu.Item>
                                                            <Link to="/logout" className={`text-red-500 hover:text-red-400 dark:hover:text-red-600 group flex rounded-md items-center w-full px-2 py-2 text-sm`}>
                                                                <LogoutIcon className="w-5 h-5 mr-2" />
                                                                Uitloggen
                                                            </Link>
                                                        </Menu.Item>
                                                    </div>
                                                </Menu.Items>
                                            </Transition>
                                        </Menu>
                                    ) : (
                                        <div>
                                            <Link to="/register" className="w-full flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-500 hover:bg-blue-600">
                                                Sign up
                                            </Link>
                                            <p className="mt-6 text-center text-base font-medium text-gray-900 dark:text-dark-text-secondary">
                                                Existing customer?{' '}
                                                <Link to="/login" className="text-blue-500 hover:text-blue-600">
                                                    Sign in
                                                </Link>
                                            </p>
                                        </div>)
                                    }
                                </div>
                            </div>
                        </div>
                    </Popover.Panel>
                </Transition>
            </Popover>
        );
    }

}
