interface DashboardWidgetProps {
  title: string;
  children: React.ReactNode;
}

export default function DashboardWidget({ title, children }: DashboardWidgetProps) {
  return (
    <section className="dashboard-widget">
      <h2>{title}</h2>
      {children}
    </section>
  );
}
