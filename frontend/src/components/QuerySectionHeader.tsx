import { Icon, type IconName } from './Icons';

type QuerySectionHeaderProps = {
  step: string;
  eyebrow: string;
  title: string;
  description: string;
  icon: IconName;
  required?: boolean;
  titleId?: string;
};

export function QuerySectionHeader({ step, eyebrow, title, description, icon, required, titleId }: QuerySectionHeaderProps) {
  return (
    <div className="panel-heading">
      <div className="panel-heading-main">
        <span className="step-number"><Icon name={icon} size={17} /><b>{step}</b></span>
        <div><span className="eyebrow">{eyebrow}</span><h2 id={titleId}>{title}</h2><p className="section-description">{description}</p></div>
      </div>
      {required && <span className="required-note">* Campos obligatorios</span>}
    </div>
  );
}
