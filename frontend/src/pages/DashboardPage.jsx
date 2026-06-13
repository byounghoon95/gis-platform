import { useQuery } from '@tanstack/react-query';
import { useEffect, useMemo, useRef, useState } from 'react';
import { NavLink } from 'react-router-dom';

import { getAdminLocations } from '../api/locations';
import { useAuthStore } from '../auth/authStore';
import AnalysisPanel from '../components/AnalysisPanel';

const radiusOptions = [300, 500, 1000];
const defaultCenter = { lat: 37.5665, lng: 126.978 };
let googleMapsPromise;

export default function DashboardPage() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const accessToken = useAuthStore((state) => state.accessToken);
  const [businessType, setBusinessType] = useState('');
  const [scoreRange, setScoreRange] = useState('all');
  const [radius, setRadius] = useState(500);
  const [selectedLocationId, setSelectedLocationId] = useState(null);
  const [pickedCoordinates, setPickedCoordinates] = useState(null);

  const locationsQuery = useQuery({
    queryKey: ['admin-locations', accessToken, businessType],
    queryFn: () => getAdminLocations(accessToken, { businessType }),
    enabled: Boolean(accessToken),
  });

  const locations = useMemo(
    () => filterLocationsByScore(locationsQuery.data ?? [], scoreRange),
    [locationsQuery.data, scoreRange],
  );
  const selectedLocation =
    locations.find((location) => location.id === selectedLocationId) ?? null;
  const businessTypes = useMemo(
    () =>
      Array.from(
        new Set(
          (locationsQuery.data ?? [])
            .map((location) => location.businessType)
            .filter(Boolean),
        ),
      ).sort(),
    [locationsQuery.data],
  );

  return (
    <main className="min-h-screen bg-zinc-100 text-zinc-950">
      <header className="border-b border-zinc-200 bg-white">
        <div className="dashboard-header mx-auto flex max-w-screen-2xl flex-col gap-4 px-4 py-4 sm:px-6">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-teal-700">
              GIS Platform
            </p>
            <h1 className="mt-1 text-2xl font-semibold">
              Location Analysis Dashboard
            </h1>
          </div>

          <nav className="flex flex-wrap items-center gap-2 text-sm font-medium">
            <NavLink
              to="/dashboard"
              className="rounded-md bg-zinc-950 px-3 py-2 text-white"
            >
              Dashboard
            </NavLink>
            <span className="rounded-md border border-zinc-200 px-3 py-2 text-zinc-600">
              {user?.name ?? user?.email ?? 'Signed in'}
            </span>
            <button
              className="rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100"
              type="button"
              onClick={logout}
            >
              Sign out
            </button>
          </nav>
        </div>
      </header>

      <div className="mx-auto flex max-w-screen-2xl flex-col gap-4 px-4 py-4 sm:px-6">
        <FilterBar
          businessType={businessType}
          businessTypes={businessTypes}
          radius={radius}
          scoreRange={scoreRange}
          onBusinessTypeChange={setBusinessType}
          onRadiusChange={setRadius}
          onScoreRangeChange={setScoreRange}
        />

        <section className="dashboard-workspace grid min-h-[calc(100vh-190px)] gap-4">
          <CandidateList
            locations={locations}
            query={locationsQuery}
            selectedLocationId={selectedLocationId}
            onSelect={setSelectedLocationId}
          />
          <MapWorkspace
            locations={locations}
            pickedCoordinates={pickedCoordinates}
            radius={radius}
            selectedLocation={selectedLocation}
            onMapClick={setPickedCoordinates}
            onSelect={setSelectedLocationId}
          />
          <DetailPanel
            accessToken={accessToken}
            pickedCoordinates={pickedCoordinates}
            radius={radius}
            selectedLocation={selectedLocation}
          />
        </section>
      </div>
    </main>
  );
}

function FilterBar({
  businessType,
  businessTypes,
  radius,
  scoreRange,
  onBusinessTypeChange,
  onRadiusChange,
  onScoreRangeChange,
}) {
  return (
    <section className="dashboard-filter grid gap-3 rounded-lg border border-zinc-200 bg-white p-3 shadow-sm">
      <label className="text-sm font-medium text-zinc-700">
        Business type
        <select
          className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-zinc-950 outline-none focus:border-teal-700 focus:ring-2 focus:ring-teal-100"
          value={businessType}
          onChange={(event) => onBusinessTypeChange(event.target.value)}
        >
          <option value="">All business types</option>
          {businessTypes.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      </label>

      <label className="text-sm font-medium text-zinc-700">
        Score range
        <select
          className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-zinc-950 outline-none focus:border-teal-700 focus:ring-2 focus:ring-teal-100"
          value={scoreRange}
          onChange={(event) => onScoreRangeChange(event.target.value)}
        >
          <option value="all">All scores</option>
          <option value="high">80-100</option>
          <option value="medium">60-79</option>
          <option value="low">Under 60</option>
        </select>
      </label>

      <fieldset className="text-sm font-medium text-zinc-700">
        <legend>Analysis radius</legend>
        <div className="mt-1 grid grid-cols-3 rounded-md border border-zinc-300 bg-zinc-50 p-1">
          {radiusOptions.map((option) => (
            <label
              className="cursor-pointer rounded px-2 py-1.5 text-center text-sm has-[:checked]:bg-zinc-950 has-[:checked]:text-white"
              key={option}
            >
              <input
                className="sr-only"
                type="radio"
                name="radius"
                checked={radius === option}
                onChange={() => onRadiusChange(option)}
              />
              {option}m
            </label>
          ))}
        </div>
      </fieldset>

      <div className="self-end rounded-md border border-teal-200 bg-teal-50 px-4 py-2 text-sm font-semibold text-teal-900">
        Circle updates immediately
      </div>
    </section>
  );
}

function CandidateList({ locations, query, selectedLocationId, onSelect }) {
  return (
    <aside className="dashboard-list flex min-h-[360px] flex-col rounded-lg border border-zinc-200 bg-white shadow-sm">
      <div className="border-b border-zinc-200 p-4">
        <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
          Candidate locations
        </p>
        <h2 className="mt-1 text-lg font-semibold">{locations.length} shown</h2>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto p-4">
        {query.isPending ? (
          <StateCard
            title="Loading locations"
            tone="neutral"
            body="Candidate locations are loading from the API."
          />
        ) : null}

        {query.isError ? (
          <StateCard
            title="Could not load locations"
            tone="warning"
            body={query.error.message}
          />
        ) : null}

        {!query.isPending && !query.isError && locations.length === 0 ? (
          <StateCard
            title="No locations"
            tone="empty"
            body="No candidate locations match the current filters yet."
          />
        ) : null}

        {locations.map((location) => {
          const score = getLocationScore(location);
          const isSelected = selectedLocationId === location.id;

          return (
            <button
              className={`w-full rounded-md border p-3 text-left transition ${
                isSelected
                  ? 'border-teal-700 bg-teal-50'
                  : 'border-zinc-200 bg-white hover:border-teal-300'
              }`}
              key={location.id}
              type="button"
              onClick={() => onSelect(location.id)}
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="font-semibold text-zinc-950">{location.name}</p>
                  <p className="mt-1 text-sm text-zinc-600">
                    {location.businessType}
                  </p>
                </div>
                <span
                  className={`rounded-full px-2 py-1 text-xs font-bold ${getScoreBadgeClass(score)}`}
                >
                  {formatScore(score)}
                </span>
              </div>
              <p className="mt-2 line-clamp-2 text-sm leading-5 text-zinc-600">
                {location.address}
              </p>
            </button>
          );
        })}
      </div>
    </aside>
  );
}

function MapWorkspace({
  locations,
  pickedCoordinates,
  radius,
  selectedLocation,
  onMapClick,
  onSelect,
}) {
  const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
  const mapElementRef = useRef(null);
  const mapRef = useRef(null);
  const markersRef = useRef(new Map());
  const circleRef = useRef(null);
  const pickMarkerRef = useRef(null);
  const [mapStatus, setMapStatus] = useState(apiKey ? 'loading' : 'missing-key');
  const selectedCoordinates = getLocationCoordinates(selectedLocation);

  useEffect(() => {
    if (!apiKey || !mapElementRef.current) {
      return undefined;
    }

    let isMounted = true;

    loadGoogleMaps(apiKey)
      .then((google) => {
        if (!isMounted || !mapElementRef.current) {
          return;
        }

        if (!mapRef.current) {
          mapRef.current = new google.maps.Map(mapElementRef.current, {
            center: selectedCoordinates ?? defaultCenter,
            disableDefaultUI: true,
            mapTypeControl: false,
            streetViewControl: false,
            fullscreenControl: true,
            zoom: selectedCoordinates ? 15 : 12,
          });

          mapRef.current.addListener('click', (event) => {
            onMapClick({
              latitude: roundCoordinate(event.latLng.lat()),
              longitude: roundCoordinate(event.latLng.lng()),
            });
          });
        }

        setMapStatus('ready');
      })
      .catch(() => {
        if (isMounted) {
          setMapStatus('error');
        }
      });

    return () => {
      isMounted = false;
    };
  }, [apiKey, onMapClick, selectedCoordinates]);

  useEffect(() => {
    const google = window.google;

    if (mapStatus !== 'ready' || !google || !mapRef.current) {
      return;
    }

    const activeIds = new Set();

    locations.forEach((location) => {
      const coordinates = getLocationCoordinates(location);

      if (!coordinates) {
        return;
      }

      activeIds.add(location.id);

      let marker = markersRef.current.get(location.id);
      const score = getLocationScore(location);

      if (!marker) {
        marker = new google.maps.Marker({
          map: mapRef.current,
          position: coordinates,
          title: location.name,
        });
        marker.addListener('click', () => onSelect(location.id));
        markersRef.current.set(location.id, marker);
      }

      marker.setPosition(coordinates);
      marker.setIcon(getMarkerIcon(google, score));
      marker.setLabel({
        color: '#ffffff',
        fontSize: '12px',
        fontWeight: '700',
        text: score == null ? '' : String(Math.round(score)),
      });
      marker.setZIndex(selectedLocation?.id === location.id ? 20 : 10);
    });

    markersRef.current.forEach((marker, id) => {
      if (!activeIds.has(id)) {
        marker.setMap(null);
        markersRef.current.delete(id);
      }
    });
  }, [locations, mapStatus, onSelect, selectedLocation]);

  useEffect(() => {
    const google = window.google;

    if (mapStatus !== 'ready' || !google || !mapRef.current) {
      return;
    }

    if (!selectedCoordinates) {
      return;
    }

    mapRef.current.panTo(selectedCoordinates);

    if (mapRef.current.getZoom() < 14) {
      mapRef.current.setZoom(14);
    }

    if (!circleRef.current) {
      circleRef.current = new google.maps.Circle({
        map: mapRef.current,
        fillColor: '#0f766e',
        fillOpacity: 0.12,
        strokeColor: '#0f766e',
        strokeOpacity: 0.75,
        strokeWeight: 2,
      });
    }

    circleRef.current.setCenter(selectedCoordinates);
    circleRef.current.setRadius(radius);
  }, [mapStatus, radius, selectedCoordinates]);

  useEffect(() => {
    const google = window.google;

    if (mapStatus !== 'ready' || !google || !mapRef.current) {
      return;
    }

    if (!pickedCoordinates) {
      return;
    }

    const position = {
      lat: pickedCoordinates.latitude,
      lng: pickedCoordinates.longitude,
    };

    if (!pickMarkerRef.current) {
      pickMarkerRef.current = new google.maps.Marker({
        icon: {
          path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
          fillColor: '#111827',
          fillOpacity: 1,
          scale: 6,
          strokeColor: '#ffffff',
          strokeWeight: 2,
        },
        map: mapRef.current,
        title: 'Picked coordinates',
      });
    }

    pickMarkerRef.current.setPosition(position);
  }, [mapStatus, pickedCoordinates]);

  return (
    <section className="dashboard-map relative min-h-[420px] overflow-hidden rounded-lg border border-zinc-200 bg-white shadow-sm">
      <div className="absolute inset-x-0 top-0 z-10 flex flex-wrap items-center justify-between gap-3 border-b border-zinc-200 bg-white/95 p-4">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
            Map workspace
          </p>
          <h2 className="text-lg font-semibold">
            {selectedLocation?.name ?? 'Select a candidate'}
          </h2>
        </div>
        <div className="rounded-full bg-teal-50 px-3 py-1 text-sm font-semibold text-teal-800">
          Radius {radius}m
        </div>
      </div>

      <div className="h-full min-h-[420px] pt-[88px]">
        <div ref={mapElementRef} className="h-full min-h-[420px]" />
      </div>

      {mapStatus !== 'ready' ? <MapStatusOverlay status={mapStatus} /> : null}
    </section>
  );
}

function MapStatusOverlay({ status }) {
  const content = {
    loading: {
      title: 'Loading Google Maps',
      body: 'The map canvas is starting up.',
      tone: 'neutral',
    },
    'missing-key': {
      title: 'Google Maps API key missing',
      body: 'Set VITE_GOOGLE_MAPS_API_KEY in the frontend environment to render the map.',
      tone: 'warning',
    },
    error: {
      title: 'Google Maps could not load',
      body: 'Check the API key, billing settings, and network access, then reload.',
      tone: 'warning',
    },
  }[status];

  return (
    <div className="absolute inset-0 grid place-items-center bg-zinc-50/95 px-6 pt-24 text-center">
      <div
        className={`max-w-md rounded-md border p-4 ${
          content.tone === 'warning'
            ? 'border-amber-200 bg-amber-50 text-amber-950'
            : 'border-zinc-200 bg-white text-zinc-700'
        }`}
      >
        <p className="font-semibold">{content.title}</p>
        <p className="mt-2 text-sm leading-6">{content.body}</p>
      </div>
    </div>
  );
}

function DetailPanel({ accessToken, pickedCoordinates, radius, selectedLocation }) {
  return (
    <aside className="dashboard-detail flex min-h-[360px] flex-col rounded-lg border border-zinc-200 bg-white shadow-sm">
      <div className="border-b border-zinc-200 p-4">
        <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
          Selected location
        </p>
        <h2 className="mt-1 text-lg font-semibold">
          {selectedLocation?.name ?? 'No location selected'}
        </h2>
      </div>

      <div className="flex flex-1 flex-col gap-4 overflow-y-auto p-4">
        <AnalysisPanel
          accessToken={accessToken}
          location={selectedLocation}
          radius={radius}
          showDetailLink
        />

        <div className="rounded-md border border-zinc-200 p-4">
          <p className="text-sm font-semibold text-zinc-900">
            Map click coordinate picker
          </p>
          <div className="mt-3 grid gap-3">
            <label className="text-sm font-medium text-zinc-700">
              Latitude
              <input
                className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-zinc-950"
                readOnly
                value={formatCoordinate(pickedCoordinates?.latitude)}
              />
            </label>
            <label className="text-sm font-medium text-zinc-700">
              Longitude
              <input
                className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-zinc-950"
                readOnly
                value={formatCoordinate(pickedCoordinates?.longitude)}
              />
            </label>
          </div>
        </div>
      </div>
    </aside>
  );
}

function StateCard({ title, body, tone }) {
  const toneClass = {
    neutral: 'border-zinc-200 bg-zinc-50 text-zinc-700',
    warning: 'border-amber-200 bg-amber-50 text-amber-900',
    empty: 'border-teal-200 bg-teal-50 text-teal-900',
  }[tone];

  return (
    <div className={`rounded-md border p-3 ${toneClass}`}>
      <p className="text-sm font-semibold">{title}</p>
      <p className="mt-1 text-sm leading-5">{body}</p>
    </div>
  );
}

function loadGoogleMaps(apiKey) {
  if (window.google?.maps) {
    return Promise.resolve(window.google);
  }

  if (!googleMapsPromise) {
    googleMapsPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      const params = new URLSearchParams({
        key: apiKey,
        v: 'weekly',
      });

      script.src = `https://maps.googleapis.com/maps/api/js?${params}`;
      script.async = true;
      script.defer = true;
      script.onload = () => resolve(window.google);
      script.onerror = reject;
      document.head.appendChild(script);
    });
  }

  return googleMapsPromise;
}

function getLocationCoordinates(location) {
  if (!location) {
    return null;
  }

  const lat = Number(location.latitude);
  const lng = Number(location.longitude);

  if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
    return null;
  }

  return { lat, lng };
}

function getLocationScore(location) {
  const value = location?.totalScore ?? location?.score ?? null;
  const score = Number(value);

  return Number.isFinite(score) ? score : null;
}

function getMarkerIcon(google, score) {
  return {
    path: google.maps.SymbolPath.CIRCLE,
    fillColor: getScoreColor(score),
    fillOpacity: 1,
    scale: 14,
    strokeColor: '#ffffff',
    strokeWeight: 3,
  };
}

function getScoreColor(score) {
  if (score == null) {
    return '#52525b';
  }

  if (score >= 80) {
    return '#0f766e';
  }

  if (score >= 60) {
    return '#ca8a04';
  }

  return '#dc2626';
}

function getScoreBadgeClass(score) {
  if (score == null) {
    return 'bg-zinc-100 text-zinc-700';
  }

  if (score >= 80) {
    return 'bg-teal-100 text-teal-900';
  }

  if (score >= 60) {
    return 'bg-amber-100 text-amber-900';
  }

  return 'bg-rose-100 text-rose-900';
}

function filterLocationsByScore(locations, scoreRange) {
  if (scoreRange === 'all') {
    return locations;
  }

  return locations.filter((location) => {
    const score = getLocationScore(location);

    if (score == null) {
      return false;
    }

    if (scoreRange === 'high') {
      return score >= 80;
    }

    if (scoreRange === 'medium') {
      return score >= 60 && score < 80;
    }

    return score < 60;
  });
}

function formatScore(score) {
  return score == null ? '--' : Math.round(score).toString();
}

function formatCoordinate(value) {
  const number = Number(value);

  return Number.isFinite(number) ? number.toFixed(6) : '';
}

function roundCoordinate(value) {
  return Number(value.toFixed(6));
}
